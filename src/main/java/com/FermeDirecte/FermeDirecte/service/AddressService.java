// src/main/java/com/FermeDirecte/FermeDirecte/service/AddressService.java
package com.FermeDirecte.FermeDirecte.service;

import com.FermeDirecte.FermeDirecte.dto.address.*;
import com.FermeDirecte.FermeDirecte.entity.Address;
import com.FermeDirecte.FermeDirecte.entity.User;
import com.FermeDirecte.FermeDirecte.exception.BusinessException;
import com.FermeDirecte.FermeDirecte.repository.AddressRepository;
import com.FermeDirecte.FermeDirecte.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AddressResponse> getMesAdresses(String email) {
        User user = getUser(email);
        return addressRepository.findByUser_Id(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse ajouter(AddressRequest request, String email) {
        User user = getUser(email);

        // Si principal, désactiver les autres
        if (Boolean.TRUE.equals(request.getPrincipal())) {
            addressRepository.findByUser_Id(user.getId())
                    .forEach(a -> { a.setPrincipal(false); addressRepository.save(a); });
        }

        Address address = Address.builder()
                .user(user)
                .rue(request.getRue())
                .ville(request.getVille())
                .codePostal(request.getCodePostal())
                .pays(request.getPays())
                .principal(Boolean.TRUE.equals(request.getPrincipal()))
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse modifier(Long id, AddressRequest request, String email) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Adresse introuvable", HttpStatus.NOT_FOUND));

        address.setRue(request.getRue());
        address.setVille(request.getVille());
        address.setCodePostal(request.getCodePostal());
        address.setPays(request.getPays());
        address.setPrincipal(Boolean.TRUE.equals(request.getPrincipal()));

        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void supprimer(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new BusinessException("Adresse introuvable", HttpStatus.NOT_FOUND);
        }
        addressRepository.deleteById(id);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable", HttpStatus.NOT_FOUND));
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .rue(a.getRue())
                .ville(a.getVille())
                .codePostal(a.getCodePostal())
                .pays(a.getPays())
                .principal(a.getPrincipal())
                .build();
    }
}
