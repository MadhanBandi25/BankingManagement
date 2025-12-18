package com.banking.service.IMPL;

import com.banking.dto.request.CustomerRequest;
import com.banking.dto.response.CustomerResponse;
import com.banking.entity.Customer;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.CustomerRepository;
import com.banking.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        Customer customer= modelMapper.map(request,Customer.class);
        Customer saved= customerRepository.save(customer);
        return modelMapper.map(saved,CustomerResponse.class);
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer= customerRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Customer Not found with id: "+ id));
        return modelMapper.map(customer,CustomerResponse.class);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {

        return customerRepository.findAll().stream()
                .map(customer -> modelMapper.map(customer,CustomerResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer=customerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Customer not found with id: " +id));
        modelMapper.map(request,customer);
        Customer saved= customerRepository.save(customer);
        return modelMapper.map(saved,CustomerResponse.class);
    }

    @Override
    public void deleteCustomer(Long id) {
        if(!customerRepository.existsById(id)){
            throw new ResourceNotFoundException("Customer not found with id: "+ id);
        }
        customerRepository.deleteById(id);
    }
}
