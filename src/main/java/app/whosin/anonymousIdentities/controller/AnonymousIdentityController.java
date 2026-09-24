package app.whosin.anonymousIdentities.controller;

import app.whosin.anonymousIdentities.dto.AnonymousIdentityResponse;
import app.whosin.anonymousIdentities.service.AnonymousIdentityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/anonymous-identities")
public class AnonymousIdentityController {
    private final AnonymousIdentityService anonymousIdentityService;

    public AnonymousIdentityController(AnonymousIdentityService anonymousIdentityService) {
        this.anonymousIdentityService = anonymousIdentityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnonymousIdentityResponse create() {
        return anonymousIdentityService.create();
    }
}
