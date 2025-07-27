package com.example.bankcards.mapper;

import com.example.bankcards.dto.card.CardCreateDTO;
import com.example.bankcards.dto.card.CardDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardUtils;
import jakarta.persistence.EntityNotFoundException;
import org.jasypt.util.text.AES256TextEncryptor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class CardMapper {
    private UserRepository userRepository;
    private AES256TextEncryptor encryptor;

    @Autowired
    protected void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    protected void setEncryptor(AES256TextEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @Mapping(source = "owner.id", target = "ownerId")
    public abstract CardDTO map(Card cardModel);

    @Mapping(source = "ownerId", qualifiedByName = "getOwnerById", target = "owner")
    @Mapping(source = "cardNumber", qualifiedByName = "maskCardNumber", target = "maskedCardNumber")
    @Mapping(source = "cardNumber", qualifiedByName = "hashCardNumber", target = "cardHash")
    @Mapping(source = "cardNumber", qualifiedByName = "encryptCardNumber", target = "encryptedCardNumber")
    public abstract Card map(CardCreateDTO cardData);

    @Named("getOwnerById")
    protected User getOwnerById(Long userId) {
        Optional<User> owner = userRepository.findById(userId);
        return owner.orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @Named("maskCardNumber")
    protected String maskCardNumber(String cardNumber) {
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    @Named("hashCardNumber")
    protected String hashCardNumber(String cardNumber) {
        return CardUtils.hash(cardNumber);
    }

    @Named("encryptCardNumber")
    protected String encryptCardNumber(String cardNumber) {
        return encryptor.encrypt(cardNumber);
    }
}
