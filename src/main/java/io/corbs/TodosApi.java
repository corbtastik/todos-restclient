package io.corbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("todos-api")
public class TodosApi {

    private static final Logger LOG = LoggerFactory.getLogger(TodosApi.class);

    private final RestTemplate restTemplate;

    @Autowired
    public TodosApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/")
    public Todo callCreate() {
        Todo todo = Todo.builder()
            .title("todos-restclient calling todos-api")
            .completed(Boolean.FALSE)
            .build();
        ResponseEntity<Todo> responseEntity = this.restTemplate
            .postForEntity("http://todos-api/todos/", todo, Todo.class);
        return responseEntity.getBody();
    }

    @GetMapping("/")
    public List<Todo> callRetrieve() {
        LOG.debug("//todos-api/todos/ to list Todo(s)");
        ResponseEntity<List> responseEntity = this.restTemplate
            .getForEntity("http://todos-api/todos/", List.class);
        return responseEntity.getBody();
    }

    @GetMapping("/{id}")
    public Todo callRetrieve(@PathVariable Integer id) {
        LOG.debug("//todos-api/todos/" + id + "/ to get Todo");
        return restTemplate.getForObject("//todos-api/todos/" + id, Todo.class);
    }

    @PostMapping("/{id}")
    public Todo callUpdate(@PathVariable Integer id, @RequestBody Todo todo) {
        return this.restTemplate
            .patchForObject("//todos-api/todos/" + id, todo, Todo.class);
    }

    @DeleteMapping("/{id}")
    public void callDelete(@PathVariable Integer id) {
        this.restTemplate
            .delete("//todos-api/todos/" + id);
    }

}


