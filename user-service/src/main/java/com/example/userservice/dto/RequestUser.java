package com.example.userservice.dto;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message ="{required.request.username}")
    @Size(min=2, message="{range.request.username}")
    private String username;

    @Email
    @NotNull(message = "{required.request.email}")
    @Size(min=2,message = "{range.request.email}")
    private String email;

    @NotNull(message="{required.request.age}")
    @Size(min = 8, message = "{range.request.pwd}")
    private String pwd;

}
