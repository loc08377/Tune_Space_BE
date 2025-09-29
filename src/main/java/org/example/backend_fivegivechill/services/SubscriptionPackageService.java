package org.example.backend_fivegivechill.services;

import org.example.backend_fivegivechill.beans.SubscriptionPackageBean;
import org.example.backend_fivegivechill.entity.SubscriptionPackageEntity;
import org.example.backend_fivegivechill.repository.SubscriptionPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionPackageService {
    @Autowired
    private SubscriptionPackageRepository subscriptionPackageRepository;

    public Page<SubscriptionPackageEntity> getAllPackagesByStatus(boolean status, String search, Pageable pageable) {
        return subscriptionPackageRepository.findAllByStatus(status, "%"+search+"%", pageable);
    }

    public Page<SubscriptionPackageEntity> getAllPackagesByStatusClient(boolean status, Pageable pageable) {
        return subscriptionPackageRepository.findAllByStatusClient(status, pageable);
    }

    public SubscriptionPackageEntity getPackageById(int id) {
        return subscriptionPackageRepository.findById(id).orElse(null);
    }

    public SubscriptionPackageEntity addPackage(SubscriptionPackageBean packageBean) {
        SubscriptionPackageEntity packageEntity = new SubscriptionPackageEntity();
        packageEntity.setName(packageBean.getName());
        packageEntity.setDuration(packageBean.getDuration());
        packageEntity.setPrice(packageBean.getPrice());
        packageEntity.setStatus(packageBean.isStatus());
        return subscriptionPackageRepository.save(packageEntity);
    }

    public SubscriptionPackageEntity existPackageAdd(SubscriptionPackageBean packageBean) {
        SubscriptionPackageEntity existPackage = subscriptionPackageRepository.existByName(packageBean.getName(), packageBean.getDuration(), packageBean.getPrice());
        return existPackage;
    }

    public SubscriptionPackageEntity updatePackage(int id, SubscriptionPackageBean packageBean) {
        SubscriptionPackageEntity exist = subscriptionPackageRepository.findById(id).orElse(null);
        if (exist != null) {
            exist.setName(packageBean.getName());
            exist.setDuration(packageBean.getDuration());
            exist.setPrice(packageBean.getPrice());
            exist.setStatus(packageBean.isStatus());
            return subscriptionPackageRepository.save(exist);
        }
        return null;
    }

    public SubscriptionPackageEntity existPackageUpdate(int id, SubscriptionPackageBean packageBean) {
        SubscriptionPackageEntity existPackage = subscriptionPackageRepository.existByNameAndId(packageBean.getName(), packageBean.getDuration(), packageBean.getPrice(), id);
        return existPackage;
    }
}

