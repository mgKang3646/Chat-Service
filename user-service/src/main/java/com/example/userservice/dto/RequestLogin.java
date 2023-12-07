package com.example.userservice.dto;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RequestLogin {

    @NotNull(message = "{required.request.login.email}")
    @Size(min=2, message = "{range.request.login.email}")
    private String email;

    @NotNull(message = "{required.request.login.password}")
    @Size(min=8, message = "{range.request.login.email}")
    private String pwd;

}
