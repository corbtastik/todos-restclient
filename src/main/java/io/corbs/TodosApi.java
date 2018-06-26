package io.corbs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("todos-api")
public class TodosApi {

    private static final Logger LOG = LoggerFactory.getLogger(TodosApi.class);

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/")
    public void testTodosAPI() {
        // call ops/info
        BuildInfo info = this.restTemplate.getForObject("http://todos-api/ops/info", BuildInfo.class);
        LOG.debug("//todos-api/ops/info=" + info);
        // create
        Todo todo = Todo.builder()
            .title("todos-restclient calling todos-api")
            .completed(Boolean.FALSE)
            .build();
        ResponseEntity<Todo> responseEntity = this.restTemplate
            .postForEntity("http://todos-api/todos/", todo, Todo.class);
        Todo result = responseEntity.getBody();
        LOG.debug("//todos-api/todos=" + result);

    }

}


