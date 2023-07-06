package md.vladdubceac.spring_learning.FirstRestApp.controllers;

import md.vladdubceac.spring_learning.FirstRestApp.models.Person;
import md.vladdubceac.spring_learning.FirstRestApp.services.PeopleService;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonErrorResponse;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonNotCreatedException;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping()
    public List<Person> getPeople(){
        return peopleService.findAll(); // Jackson converts object to JSON
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id")long id){
        return peopleService.findOne(id); // Jackson converts object to JSON
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person,
                                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            fieldErrors.stream().forEach(f -> errorMessage.append(f.getField()).append("-").append(f.getDefaultMessage()).append(";\n"));

            throw new PersonNotCreatedException(errorMessage.toString());
        }
        peopleService.save(person);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse("Person with this id wasn't found!", System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(), System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
