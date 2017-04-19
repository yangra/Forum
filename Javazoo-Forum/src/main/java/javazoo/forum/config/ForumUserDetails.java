package javazoo.forum.config;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import javazoo.forum.entity.User;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

public class ForumUserDetails extends User implements UserDetails{

    private ArrayList<String>roles;
    private User user;

    public ForumUserDetails(User user, ArrayList<String> roles){
        super(user.getUsername(),user.getEmail(), user.getFullName(), user.getPassword(), user.getImagePath());

        this.roles = roles;
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        String userRoles = StringUtils.collectionToCommaDelimitedString(this.roles);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(userRoles);
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }

}
