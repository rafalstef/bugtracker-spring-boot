package dev.bugtracker.demo.repository;

import dev.bugtracker.demo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepo extends JpaRepository<Project, Long> {


    // SELECT * FROM project p WHERE p.id IN ( SELECT project_id FROM project_user  WHERE user_id = ?1 )
    @Query("select p from Project p inner join p.users u where u.id=?1")
    List<Project> findWithAssignedUser(Long userId);

    @Query("SELECT p FROM Project p WHERE p.createdBy.id = ?1")
    List<Project> findAllCreatedBy(Long userId);
}
