package com.lamdayne.todo.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SuccessCode {

    TASK_CREATE_SUCCESS("TASK_CREATE_SUCCESS", "Task created successfully"),
    TASK_READ_SUCCESS("TASK_READ_SUCCESS", "Task read successfully"),
    TASK_UPDATE_SUCCESS("TASK_UPDATE_SUCCESS", "Task updated successfully"),
    TASK_DELETE_SUCCESS("TASK_DELETE_SUCCESS", "Task delete successfully"),


    ;

    private String code;
    private String defaultMessage;

}
