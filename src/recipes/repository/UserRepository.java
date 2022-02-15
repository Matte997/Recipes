package recipes.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import recipes.entity.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmail(String email);

}
