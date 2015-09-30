package org.qzerver.web.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping
public class ExceptionController {

    @RequestMapping(value = "/exception", method = RequestMethod.GET)
    public String handle(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception
    {
        throw new IllegalStateException("Just an exception for testing purposes");
    }

}
