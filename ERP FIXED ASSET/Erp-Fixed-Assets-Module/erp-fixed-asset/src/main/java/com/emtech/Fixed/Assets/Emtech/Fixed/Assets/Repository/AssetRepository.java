package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByAllocatedToIsNull();
}
