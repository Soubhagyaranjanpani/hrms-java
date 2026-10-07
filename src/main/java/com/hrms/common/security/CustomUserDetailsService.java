package com.hrms.common.security;



import com.hrms.employee.domain.Employee;
import com.hrms.employee.infrastructure.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmailAndIsActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with email: " + username));

        return employee;
    }

    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByPhoneAndIsActiveTrue(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Employee not found with phone: " + phone));

        return employee;
    }
}
