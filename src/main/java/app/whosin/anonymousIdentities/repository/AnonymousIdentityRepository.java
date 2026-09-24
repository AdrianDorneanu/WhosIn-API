package app.whosin.anonymousIdentities.repository;

import app.whosin.anonymousIdentities.entity.AnonymousIdentity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnonymousIdentityRepository extends JpaRepository<AnonymousIdentity, UUID> {
}
