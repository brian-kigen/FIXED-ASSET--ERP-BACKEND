package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.Asset;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepreciationService {
    @Autowired
    private AssetRepository assetRepository;

    @Scheduled(cron = "0 0 0 1 1 ?") // Run annually on January 1st
    public void updateDepreciation() {
        List<Asset> assets = assetRepository.findAll();
        for (Asset asset : assets) {
            Integer annualDepreciation = asset.calculateAnnualDepreciation();
            Integer newAccumulatedDepreciation = asset.getAccumulatedDepreciation()+annualDepreciation;
            asset.setAccumulatedDepreciation(newAccumulatedDepreciation);
            assetRepository.save(asset);
        }
    }

}
