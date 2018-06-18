package io.corbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class TodosClientAPI {

    private static Logger LOG = LoggerFactory.getLogger(TodosClientApp.class);

    /**
     * RestTemplate HTTP Client - injected in constructor
     */
    private RestTemplate restTemplate;

    @Value("${todos.target.endpoint}")
    private String targetEndpoint;

    @Autowired
    public TodosClientAPI(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/")
    public Todo createRandomTodo() {
        Todo todo = Todo.builder()
            .completed(Boolean.FALSE)
            .build();
        long now = System.currentTimeMillis();
        LOG.debug("Calling " + targetEndpoint +  " to create Todo " + todo.toString());
        ResponseEntity<Todo> responseEntity = this.restTemplate
            .postForEntity(targetEndpoint + "/", todo, Todo.class);
        Todo result = responseEntity.getBody();
        LOG.debug("Completed " + targetEndpoint +  " create Todo " + result.toString()
            + " in " + (System.currentTimeMillis() - now) + " milliseconds.");
        return result;
    }

    @GetMapping("/")
    public List<Todo> retrieve() {
        long now = System.currentTimeMillis();
        LOG.debug("Calling " + targetEndpoint +  " to list Todo(s)");
        ResponseEntity<List> responseEntity = this.restTemplate
            .getForEntity(targetEndpoint + "/", List.class);
        List<Todo> result = responseEntity.getBody();
        LOG.debug("Completed " + targetEndpoint +  " listing " + result.size() + " Todo(s)"
            + " in " + (System.currentTimeMillis() - now) + " milliseconds.");
        return result;
    }

    @GetMapping("/{id}")
    public Todo retrieve(@PathVariable Integer id) {
        long now = System.currentTimeMillis();
        String url = targetEndpoint + "/" + id + "/";
        LOG.debug("Calling " + url + " to get Todo");
        Todo todo = restTemplate.getForObject(url, Todo.class);
        LOG.debug("Completed " + url +  " getting Todo " + todo
                + " in " + (System.currentTimeMillis() - now) + " milliseconds.");
        return todo;
    }
}
