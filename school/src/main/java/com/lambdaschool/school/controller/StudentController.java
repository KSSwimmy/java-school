package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.ErrorDetails;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    // Please note there is no way to add students to course yet!

    //localhost:2019/students/students/?page=1&size=1

    //Value and description is the same thing.........responseContainer tells what were returning
    @ApiOperation(value = "returns all Students with paging Ability", responseContainer = "List")
    @ApiImplicitParams({@ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", value = "Results page you want to retrieve (0..N)"),
                        @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
                        @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query", value = "Sorting criteria in the format: property(,asc|desc). " + "Default sort order is ascending. " + "Multiple sort criteria are supported.")})

    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(HttpServletRequest request, @PageableDefault(page = 0, size = 3) Pageable pageable)
    {
        logger.trace(request.getMethod() + request.getRequestURI() + " accessed");

        List<Student> myStudents = studentService.findAll(pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }


    //localhost:2019/students/Student/{studentId}
    @ApiOperation(value = "Retrieves a Student associated with the StudentId", response = Student.class)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Student Found", response = Student.class),
                            @ApiResponse(code = 404, message = "Student NOT Found", response = ErrorDetails.class)})

    @GetMapping(value = "/Student/{StudentId}", produces = {"application/json"})
    public ResponseEntity<?> getStudentById(@ApiParam(value = "Student ID", required = true, example = "1")
                                                @PathVariable Long StudentId, HttpServletRequest request)
    {
        logger.trace(request.getMethod() + request.getRequestURI() + " accessed");

        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    //localhost:2019/students/student/namelike/{name}
    @ApiOperation(value = "Retrieves a Student associated with the name", response = Student.class)
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Student Found", response = Student.class),
                            @ApiResponse(code = 404, message = "Student NOT Found", response = ErrorDetails.class)})

    @GetMapping(value = "/student/namelike/{name}",
            produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(@PathVariable String name, HttpServletRequest request)
    {
        logger.trace(request.getMethod() + request.getRequestURI() + " accessed");

        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }


    //localhost:2019/students/Student
    @ApiOperation(value = "Creates a new Student", notes = "The newly created Student Id will be sent in the location header", response = void.class)
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Student Created", response = void.class),
                            @ApiResponse(code = 500, message = "Error creating Student", response = ErrorDetails.class)})
    @PostMapping(value = "/Student", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> addNewStudent(@Valid @RequestBody Student newStudent, HttpServletRequest request) throws URISyntaxException
    {
        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI =
                ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    //localhost:2019/students/Student/{Studentid}
    @PutMapping(value = "/Student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @RequestBody
                    Student updateStudent,
            @PathVariable
                    long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //localhost:2019/students/Student/{Studentid}
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
