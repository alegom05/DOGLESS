package com.example.springdogless.Repository;

import com.example.springdogless.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface LiveMessagesRepository extends JpaRepository<LiveMessages,Integer> {
    List<LiveMessages> findBySalaOrderByFechaenvioAsc(String sala);

    @Query(value = "SELECT DISTINCT sala FROM dogless.livemessages WHERE sala IS NOT NULL;", nativeQuery = true)
    List<String> findRooms();

    LiveMessages findBySala(String room);
}