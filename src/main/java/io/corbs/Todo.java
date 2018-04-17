package io.corbs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Todo {
    private Integer id;
    private String title = "";
    private Boolean completed = false;
    private Integer order = -1;
    private String url = "http://localhost:8080";
}