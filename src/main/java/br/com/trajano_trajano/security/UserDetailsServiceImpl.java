package br.com.trajano_trajano.security;

import br.com.trajano_trajano.database.repository.UserRepository;
import br.com.trajano_trajano.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        return userRepository.findByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario nao encontrado"));
    }
}
