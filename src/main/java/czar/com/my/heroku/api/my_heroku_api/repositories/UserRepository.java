package czar.com.my.heroku.api.my_heroku_api.repositories;

import czar.com.my.heroku.api.my_heroku_api.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}
