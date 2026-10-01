package app.whosin.anonymousIdentities.service;

import app.whosin.anonymousIdentities.dto.AnonymousIdentityResponse;
import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import app.whosin.anonymousIdentities.exception.InvalidAnonymousTokenException;
import app.whosin.anonymousIdentities.repository.AnonymousIdentityRepository;
import app.whosin.auth.exception.InvalidCredentialsException;
import app.whosin.common.exception.FeatureNotImplementedException;
import app.whosin.users.entity.User;
import app.whosin.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AnonymousIdentityService {
    private final AnonymousIdentityRepository anonymousIdentityRepository;
    private final AnonymousIdentityJwtService anonymousIdentityJwtService;
    private final UserRepository userRepository;

    public AnonymousIdentityService(AnonymousIdentityRepository anonymousIdentityRepository, AnonymousIdentityJwtService anonymousIdentityJwtService, UserRepository userRepository) {
        this.anonymousIdentityRepository = anonymousIdentityRepository;
        this.anonymousIdentityJwtService = anonymousIdentityJwtService;
        this.userRepository = userRepository;
    }

    @Transactional
    public AnonymousIdentityResponse create() {
        AnonymousIdentity anonymousIdentity = new AnonymousIdentity();

        AnonymousIdentity savedAnonymousIdentity = anonymousIdentityRepository.save(anonymousIdentity);

        String token = anonymousIdentityJwtService.createToken(savedAnonymousIdentity);

        return new AnonymousIdentityResponse(token);
    }

    @Transactional
    public void claim(UUID userId, String anonymousToken) {
        User user = userRepository.findById(userId).orElseThrow(() -> new InvalidCredentialsException("Authenticated user no longer exists"));

        UUID anonymousIdentityId = anonymousIdentityJwtService.validateToken(anonymousToken);

        AnonymousIdentity anonymousIdentity = anonymousIdentityRepository.findById(anonymousIdentityId).orElseThrow(() -> new InvalidAnonymousTokenException());

        throw new FeatureNotImplementedException("Claiming anonymous identities is not implemented yet");
    }
}
