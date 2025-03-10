package authservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import authservice.entities.UserInfo;
import authservice.model.UserInfoDto;
import authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Couldn't find the user..!!");
        }
        return new CustomUserDetails(user);
    }


    public UserInfo checkUserInfoAlreadyExist(UserInfoDto userInfoDto) {
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public Boolean signUpUser(UserInfoDto userInfoDto) {
        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        if (Objects.nonNull(checkUserInfoAlreadyExist(userInfoDto))) {
            return false;
        }
        String userId = UUID.randomUUID().toString();
        UserInfo userInfo = UserInfo.builder().userId(userId)
                .username(userInfoDto.getUsername())
                .password(userInfoDto.getPassword())
                .roles(new HashSet<>())
                .build();
        userRepository.save(userInfo);
        return true;
    }

    public String getUserByUsername(String userName){
        return Optional.of(userRepository.findByUsername(userName)).map(UserInfo::getUserId).orElse(null);
    }
}
