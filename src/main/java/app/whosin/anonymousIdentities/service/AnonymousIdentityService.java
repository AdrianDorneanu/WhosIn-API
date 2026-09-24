package app.whosin.anonymousIdentities.service;

import app.whosin.anonymousIdentities.dto.AnonymousIdentityResponse;
import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import app.whosin.anonymousIdentities.repository.AnonymousIdentityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnonymousIdentityService {
    private final AnonymousIdentityRepository anonymousIdentityRepository;
    private final AnonymousIdentityJwtService anonymousIdentityJwtService;

    public AnonymousIdentityService(AnonymousIdentityRepository anonymousIdentityRepository, AnonymousIdentityJwtService anonymousIdentityJwtService) {
        this.anonymousIdentityRepository = anonymousIdentityRepository;
        this.anonymousIdentityJwtService = anonymousIdentityJwtService;
    }

    @Transactional
    public AnonymousIdentityResponse create() {
        AnonymousIdentity anonymousIdentity = new AnonymousIdentity();

        AnonymousIdentity savedAnonymousIdentity = anonymousIdentityRepository.save(anonymousIdentity);

        String token = anonymousIdentityJwtService.createToken(savedAnonymousIdentity);

        return new AnonymousIdentityResponse(token);
    }
}
