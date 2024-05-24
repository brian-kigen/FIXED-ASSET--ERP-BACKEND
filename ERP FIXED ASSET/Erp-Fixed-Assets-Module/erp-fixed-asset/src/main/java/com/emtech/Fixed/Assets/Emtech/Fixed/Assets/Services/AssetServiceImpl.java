package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.Asset;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.AssetRequest;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.User;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.AssetRepository;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.AssetRequestRepository;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.AssetRequest.Status.PENDING;

@Service
public class AssetServiceImpl implements AssetService {
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private AssetRequestRepository assetRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public Asset createAsset(Asset asset) {
        // Initial values will be automatically set by Asset entity
        return assetRepository.save(asset);
    }

//    @Override
//    public void receiveAsset(Long assetId, BigDecimal value) {
//
//    }

    @Override
    @PreAuthorize("hasRole('MANAGER') or hasRole('OFFICER')")
    public void receiveAsset(Long assetId) {
        Optional<Asset> optionalAsset = assetRepository.findById(assetId);
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            if (asset.getAllocatedTo() != null && asset.getAllocatedTo().getUsername().equals(currentUsername)) {
                asset.setReceived(true);
                assetRepository.save(asset);
            } else {
                throw new IllegalArgumentException("Asset is not allocated to the current user.");
            }
        } else {
            throw new IllegalArgumentException("Asset not found with id: " + assetId);
        }
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void disposeAsset(Long assetId) {
        Optional<Asset> optionalAsset = assetRepository.findById(assetId);
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            // Dispose the asset at salvage value
            asset.disposeAtSalvageValue();
            assetRepository.save(asset);
        } else {
            throw new IllegalArgumentException("Asset not found with id: " + assetId);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OFFICER')")
    public Integer calculateProfitOrLoss(Long assetId) {
        Optional<Asset> optionalAsset = assetRepository.findById(assetId);
        if (optionalAsset.isPresent()) {
            Asset asset = optionalAsset.get();
            return asset.calculateProfitOrLoss();
        } else {
            throw new IllegalArgumentException("Asset not found with id: " + assetId);
        }

    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void allocateAsset(Long assetId, String allocatedToUsername) {
        Optional<Asset> optionalAsset = assetRepository.findById(assetId);
        Optional<User> optionalUser = userRepository.findByUsername(allocatedToUsername);

        if (optionalAsset.isPresent() && optionalUser.isPresent()) {
            Asset asset = optionalAsset.get();
            User user = optionalUser.get();
            asset.setAllocatedTo(user);
            assetRepository.save(asset);
        } else {
            throw new IllegalArgumentException("Asset or User not found with the provided id or username.");
        }
    }
    public AssetRequest requestAsset(Long assetId, Long userId) {
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new RuntimeException("Asset not found"));
        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setAsset(asset);
        assetRequest.setUserId(userId);
        assetRequest.setStatus(PENDING);
        return assetRequestRepository.save(assetRequest);
    }

    public List<AssetRequest> getAssetRequestsByUserId(Long userId) {
        return assetRequestRepository.findByUserId(userId);
    }

    public List<AssetRequest> getAllAssetRequests() {
        return assetRequestRepository.findAll();
    }
    public AssetRequest createPendingAssetRequest(Long assetId, Long userId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        AssetRequest assetRequest = new AssetRequest();
        assetRequest.setAsset(asset);
        assetRequest.setUserId(userId);
        assetRequest.setStatus(PENDING); // Assuming Status is an enum defined in AssetRequest class
        return assetRequestRepository.save(assetRequest);
    }

    @Transactional
    public void acceptAssetRequest(Long requestId) {
        // Retrieve the asset request from the repository
        AssetRequest assetRequest = assetRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Asset request not found with id: " + requestId));

        // Update the status of the asset request to ACCEPTED
        assetRequest.setStatus(AssetRequest.Status.ACCEPTED);

        // You can add further business logic here, such as updating the asset's status, etc.

        // Save the updated asset request in the repository
        assetRequestRepository.save(assetRequest);
    }

    @Transactional
    public void rejectAssetRequest(Long requestId) {
        // Retrieve the asset request from the repository
        AssetRequest assetRequest = assetRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Asset request not found with id: " + requestId));

        // Update the status of the asset request to REJECTED
        assetRequest.setStatus(AssetRequest.Status.REJECTED);

        // You can add further business logic here, such as notifying the user, etc.

        // Save the updated asset request in the repository
        assetRequestRepository.save(assetRequest);
    }
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OFFICER')")
    public List<Asset> getAllUnallocatedAssets() {
        return assetRepository.findByAllocatedToIsNull();
    }


}
