package de.avtest.testaufgabe.juniortask.repository;

import de.avtest.testaufgabe.juniortask.data.dbo.GameBoardDBO;
import org.springframework.data.repository.CrudRepository;

public interface GameBoardRepository extends CrudRepository<GameBoardDBO, String> {
}
