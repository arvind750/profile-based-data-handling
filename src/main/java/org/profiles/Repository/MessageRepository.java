package org.profiles.Repository;

import org.profiles.Model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Integer> {
List<Message> findAllByProfileName(String profileName);
}

