package io.corbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class TodoClientAPI {

    private static final Logger LOG = LoggerFactory.getLogger(TodoClientAPI.class);

    private final RestTemplate restTemplate;

    @Autowired
    public TodoClientAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/")
    public Todo createTodo(@RequestBody Todo todo) {
        ResponseEntity<Todo> responseEntity = this.restTemplate.postForEntity("/todos/", todo, Todo.class);
        return responseEntity.getBody();
    }

    @GetMapping("/")
    public List<Todo> retrieve() {
        ResponseEntity<List> responseEntity = this.restTemplate.getForEntity("/todos/", List.class);
        return responseEntity.getBody();
    }

    @GetMapping("/{id}")
    public Todo retrieve(@PathVariable Integer id) {
        return restTemplate.getForObject("/todos/{id}", Todo.class, id);
    }

    @DeleteMapping("/")
    public void delete() {
        this.restTemplate.delete("/todos/");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        this.restTemplate.delete("/todos/{id}", id);
    }

    @PatchMapping("/{id}")
    public Todo update(@PathVariable Integer id, @RequestBody Todo todo) {
        return this.restTemplate.patchForObject("/todos/{id}", todo, Todo.class, id);
    }
    
}


