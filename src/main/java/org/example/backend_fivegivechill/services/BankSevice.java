package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.entity.BankEntity;
import org.example.backend_fivegivechill.entity.UserEntity;
import org.example.backend_fivegivechill.repository.BankRepository;
import org.example.backend_fivegivechill.repository.UserRepository;
import org.example.backend_fivegivechill.response.BankRespone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class BankSevice {
    @Autowired
    BankRepository bankRepository;

    @Autowired
    UserRepository userRepository;

    public String CreateBank(int user, BankRespone bankRespone) {
        try {
            UserEntity userEntity = userRepository.findById(user).get();

            BankEntity bankEntity = bankRepository.checkInformation(bankRespone.getNumberAccount(), bankRespone.getNameAccount());
            if (bankEntity != null) {
                return "Ngân hàng đã được sử dụng";
            }
            bankRepository.save(new BankEntity(
                    0,
                    userEntity,
                    bankRespone.getNumberAccount(),
                    bankRespone.getNameAccount(),
                    bankRespone.getCreateDate(),
                    true
            ));
            return "Liên kết ngân hành thành công";
        } catch (Exception e) {
            System.out.println("sssssssssssssssssssssssssssss " + e.getMessage());
            return "Không thể liên kết ngân hàng";
        }
    }

    //    public List<BankEntity> bankcardByUser(int user) {
//        try {
//            UserEntity userEntity = userRepository.findById(user).get();
//
//
//        } catch (Exception e) {
//            System.out.println("sssssssssssssssssssssssssssss " + e.getMessage());
//            return "Không thể liên kết ngân hàng";
//        }
//    }
//    @Transactional
//    public boolean updateBankStatus(List<Integer> ids) {
//        List<BankEntity> banks = bankRepository.findAllById(ids);
//
//        if (banks.isEmpty()) {
//            return false;
//        }
//
//        for (BankEntity bank : banks) {
//            bank.setStatus(false);
//        }
//
//        bankRepository.saveAll(banks);
//        return true;
//    }

    @Transactional
    public void updateStatus(List<Integer> ids) {
        int updatedRows = bankRepository.disableBanks(ids);
        System.out.println("Số bản ghi update: " + updatedRows);
    }
}
