package com.lamdayne.todo.dto.response;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Data
@Builder
public class TodoStatsResponse implements Serializable {
    private long total;
    private long completed;
    private long active;
}
