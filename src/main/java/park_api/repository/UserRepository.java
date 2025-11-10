package park_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import park_api.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
