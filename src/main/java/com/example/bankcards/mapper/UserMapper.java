package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.UserCreateDTO;
import com.example.bankcards.dto.user.UserDTO;
import com.example.bankcards.dto.user.UserRolesUpdateDTO;
import com.example.bankcards.dto.user.UserUpdateDTO;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        uses = {JsonNullableMapper.class},
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {

    @Mapping(source = "cards", qualifiedByName = "extractMaskedCardNumbers", target = "maskedCardNumbers")
    public abstract UserDTO map(User userModel);

    @Mapping(source = "password", qualifiedByName = "encryptPassword", target = "password")
    public abstract User map(UserCreateDTO userData);

    @Mapping(source = "password", qualifiedByName = "encryptPassword", target = "password")
    public abstract void update(UserUpdateDTO userData, @MappingTarget User userModel);

    public abstract void updateRoles(UserRolesUpdateDTO userData, @MappingTarget User userModel);

    @Named("extractMaskedCardNumbers")
    protected List<String> extractMaskedCardNumbers(List<Card> cards) {
        return cards.stream()
                .map(Card::getMaskedCardNumber)
                .toList();
    }

    @Named("encryptPassword")
    protected String encryptPassword(String password) {
//        TODO: запилить пассворд енкодер
        return "encodedPassword";
    }
}
