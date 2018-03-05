//package ru.strcss.projects.moneycalc.moneycalcserver.configuration.securiy.service;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//import ru.strcss.projects.moneycalc.enitities.Access;
//import ru.strcss.projects.moneycalc.moneycalcserver.dbconnection.RegistrationDBConnection;
//
//import java.util.Collection;
//import java.util.HashSet;
//
//@Component
//@Service
//public class UserAccountService implements UserDetailsService {
//
//    RegistrationDBConnection registrationDBConnection;
//
//    public UserAccountService(RegistrationDBConnection registrationDBConnection) {
//        this.registrationDBConnection = registrationDBConnection;
//    }
//
//    //UserDetails userDetails = new User()
//    public User loadUserByUsername(String login) {
//
//        //UserAccount userAccount= userAccountDbRepository.getUserAccountByUserName(login);
//        Access userAccount = registrationDBConnection.getAccessByLogin(login);
//        Collection<GrantedAuthority> gas;
//        gas = new HashSet<>();
//        for (int i = 0; i < userAccount.getRoles().size(); i++) {
//
//            JSONObject jsonObject = new JSONObject(userAccount.getRoles().get(i));
//            String role = jsonObject.getString("role");
//            gas.add(new SimpleGrantedAuthority(role));
//        }
////        return new CustomUserDetails(userAccount.getLogin(), userAccount.getPassword(), gas, userAccount.getEnabled());
//        return new CustomUserDetails(userAccount.getLogin(), userAccount.getPassword(), gas, true);
//    }
//
//
//}
