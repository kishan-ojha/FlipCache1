package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TimeBasedKeyNode<T> {
    private T key;
    private LocalDateTime localDateTime;
}
