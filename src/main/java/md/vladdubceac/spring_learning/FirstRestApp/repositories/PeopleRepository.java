package md.vladdubceac.spring_learning.FirstRestApp.repositories;

import md.vladdubceac.spring_learning.FirstRestApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeopleRepository extends JpaRepository<Person,Long> {
}
