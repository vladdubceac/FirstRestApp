package md.vladdubceac.spring_learning.FirstRestApp.controllers;

import md.vladdubceac.spring_learning.FirstRestApp.dtos.PersonDTO;
import md.vladdubceac.spring_learning.FirstRestApp.models.Person;
import md.vladdubceac.spring_learning.FirstRestApp.services.PeopleService;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonErrorResponse;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonNotCreatedException;
import md.vladdubceac.spring_learning.FirstRestApp.util.PersonNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getPeople(){
        List<Person> people =peopleService.findAll(); // Jackson converts object to JSON
        return people.stream().map(this::convertToPersonDTO).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id")long id){
        Person foundedPerson = peopleService.findOne(id); // Jackson converts object to JSON
        return convertToPersonDTO(foundedPerson);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            fieldErrors.stream().forEach(f -> errorMessage.append(f.getField()).append("-").append(f.getDefaultMessage()).append(";\n"));

            throw new PersonNotCreatedException(errorMessage.toString());
        }
        peopleService.save(convertToPerson(personDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
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
