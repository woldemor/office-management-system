package com.netcracker.service.sub;

import com.netcracker.exception.CannotCreateSubRequestException;
import com.netcracker.model.entity.Person;
import com.netcracker.model.entity.Priority;
import com.netcracker.model.entity.Request;
import com.netcracker.model.entity.Status;
import com.netcracker.repository.data.interfaces.PersonRepository;
import com.netcracker.repository.data.interfaces.PriorityRepository;
import com.netcracker.repository.data.interfaces.RequestRepository;
import com.netcracker.repository.data.interfaces.StatusRepository;
import com.netcracker.util.enums.status.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class SubRequestServiceImpl {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private PriorityRepository priorityRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private PersonRepository personRepository;

    public Request createRequest(Long parenId, Request sub, String principalEmail) throws CannotCreateSubRequestException {

        Request parent = requestRepository.findOne(parenId)
                .orElseThrow(() -> new CannotCreateSubRequestException("Parent not found."));

        if (principalEmail == null){
            throw new CannotCreateSubRequestException("Person not found.");
        }
        Person person = personRepository.findPersonByEmail(principalEmail)
                .orElseThrow(() -> new CannotCreateSubRequestException("Person not found."));

        if (sub.getPriority()!=null){
            priorityRepository.findOne(sub.getPriority().getId())
                    .orElseThrow(() -> new CannotCreateSubRequestException("Invalid priority."));
        } else {
            Priority normal = priorityRepository.findPriorityByName("NORMAL")
                    .orElseThrow(() -> new CannotCreateSubRequestException("Server error."));
            sub.setPriority(normal);
        }

        Status statusFree = statusRepository.findStatusByName(StatusEnum.FREE.getName())
                .orElseThrow(() -> new CannotCreateSubRequestException("Invalid status."));

        sub.setParent(parent);
        sub.setEmployee(person);
        sub.setStatus(statusFree);
        sub.setCreationTime(new Timestamp(new Date().getTime()));

        if (parent.getEstimate()!=null && sub.getEstimate()!=null){
            if (parent.getEstimate().before(sub.getEstimate()));{
                throw new CannotCreateSubRequestException("Invalid estimate.");
            }
        }

        return requestRepository.save(sub)
                .orElseThrow(() -> new CannotCreateSubRequestException("Server error."));
    }

    public Request updateRequest(Long subId, Long parenId, Request sub) throws CannotCreateSubRequestException {

        Request subRequest = requestRepository.findOne(subId)
                .orElseThrow(() -> new CannotCreateSubRequestException("Subrequest not found."));

        if (subRequest.getParent()==null){
            throw new CannotCreateSubRequestException("This request is not a subrequest.");
        }

        Request parent = requestRepository.findOne(parenId)
                .orElseThrow(() -> new CannotCreateSubRequestException("Parent not found."));

        if (subRequest.getParent().getId() != parent.getId()){
            throw new CannotCreateSubRequestException("This request is not a subrequest.");
        }

        if (sub.getName()!=null&&sub.getName().length()>3){
            subRequest.setName(sub.getName());
        }

        subRequest.setDescription(sub.getDescription());

        if (sub.getStatus()!=null){
            Status newStatus = statusRepository.findOne(sub.getStatus().getId())
                    .orElseThrow(() -> new CannotCreateSubRequestException("Invalid status."));
            subRequest.setStatus(newStatus);
        }

        if (sub.getPriority()!=null){
            Priority newPriority = priorityRepository.findOne(sub.getPriority().getId())
                    .orElseThrow(() -> new CannotCreateSubRequestException("Invalid priority."));
            subRequest.setPriority(newPriority);
        }

        if (sub.getEstimate()!=null){
            Timestamp newEstimate = sub.getEstimate();
            if (subRequest.getCreationTime().after(newEstimate)){
                throw new CannotCreateSubRequestException("Invalid estimate.");
            }
            if (parent.getEstimate()!=null&&parent.getEstimate().before(newEstimate)){
                throw new CannotCreateSubRequestException("Invalid estimate.");
            }
            subRequest.setEstimate(newEstimate);
        } else {
            subRequest.setEstimate(null);
        }
        return requestRepository.updateRequest(subRequest)
                .orElseThrow(() -> new CannotCreateSubRequestException("Server error."));
    }

    public List<Request> getAllSubRequest(Long parentId){
        return requestRepository.getAllSubRequest(parentId);
    }

    public void deleteSubRequest(Long parentId, Long subId) throws CannotCreateSubRequestException {
        Request parent = requestRepository.findOne(parentId)
                .orElseThrow(() -> new CannotCreateSubRequestException("Parent not found."));

        Request sub = requestRepository.findOne(subId)
                .orElseThrow(() -> new CannotCreateSubRequestException("Subrequest not found."));

        if (sub.getParent().getId() == parent.getId()){
            requestRepository.delete(subId);
        } else {
            throw new CannotCreateSubRequestException("Subrequest not found.");
        }
    }

    public List<Status> getStatuses(){
        return statusRepository.findAll();
    }

    public List<Priority> getPriorities(){
        return priorityRepository.findAll();
    }
}
