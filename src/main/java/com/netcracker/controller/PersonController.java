package com.netcracker.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.netcracker.exception.CannotUpdatePersonException;
import com.netcracker.exception.IllegalAccessException;
import com.netcracker.exception.ResourceNotFoundException;
import com.netcracker.model.dto.FullPersonDTO;
import com.netcracker.model.dto.Page;
import com.netcracker.model.dto.PersonDTO;
import com.netcracker.model.entity.Person;
import com.netcracker.model.validation.CreateValidatorGroup;
import com.netcracker.model.view.View;
import com.netcracker.repository.common.Pageable;
import com.netcracker.service.person.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.netcracker.controller.RegistrationController.JSON_MEDIA_TYPE;

@RestController
@RequestMapping("/api/person")
public class PersonController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonService personService;

    @GetMapping("/managers/{namePattern}")
    public ResponseEntity<?> getManagers(@PathVariable(required = false) String namePattern,
                                         Pageable pageable) {
        return ResponseEntity.ok(personService.getManagers(pageable, namePattern));
    }

    @GetMapping("/users/{namePattern}")
    public ResponseEntity<?> getUsersByNamePattern(@PathVariable(required = false) String namePattern,
                                         Pageable pageable) {
        return ResponseEntity.ok(personService.getUsersByNamePattern(pageable, namePattern));
    }


    @PutMapping(produces = JSON_MEDIA_TYPE, value = "/{personId}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long personId,
                                               @Validated(CreateValidatorGroup.class) @RequestBody PersonDTO personDTO) throws ResourceNotFoundException, IllegalAccessException, CannotUpdatePersonException {
        Person currentUser = personDTO.toPerson();
        currentUser.setId(personId);
        Optional<Person> person = personService.updatePerson(currentUser, personId);
        if(!person.isPresent())
            new ResponseEntity<>(currentUser, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }


    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list/{roleId}")
    public ResponseEntity<?> getPersonListByRole(@PathVariable Integer roleId, Pageable pageable) {
        Page<Person> personPage = personService.getPersonListByRole(roleId, pageable);

        return ResponseEntity.ok(personPage);
    }

    @GetMapping(produces = JSON_MEDIA_TYPE, value = "/list")
    public ResponseEntity<?> getPersonList(Pageable pageable) {
        Page<Person> personPage = personService.getPersonList(pageable);

        return ResponseEntity.ok(personPage);
    }

    @GetMapping("/{personId}")
    @JsonView(View.Public.class)
    @ResponseStatus(HttpStatus.OK)
    public FullPersonDTO getPersonById(@PathVariable("personId") Long id) throws ResourceNotFoundException {
        Optional<Person> personOptional = personService.getPersonById(id);
        if (!personOptional.isPresent()) {
            LOGGER.error("Person with id {} not exist", id);
            throw new ResourceNotFoundException("Can't find person with id " + id);
        } else
            return new FullPersonDTO(personOptional.get());
    }

}
