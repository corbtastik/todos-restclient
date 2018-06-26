package io.corbs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class BuildInfo {
    Info build;
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class Info {
    String group;
    String artifact;
    String name;
    String version;
    Instant time;
}


