package com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Controllers;

import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.Asset;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Entity.AssetRequest;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Repository.UserRepository;
import com.emtech.Fixed.Assets.Emtech.Fixed.Assets.Services.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://allowed-origin.com")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserRepository userRepository;


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/create")
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        Asset createdAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(createdAsset);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OFFICER')")
    @PostMapping("/{assetId}/receive")
    public ResponseEntity<String> receiveAsset(@PathVariable Long assetId) {
        assetService.receiveAsset(assetId);
        return ResponseEntity.ok("Asset received successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{assetId}/dispose")
    public ResponseEntity<String> disposeAsset(@PathVariable Long assetId) {
        assetService.disposeAsset(assetId);
        return ResponseEntity.ok("Asset disposed successfully.");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('OFFICER')")
    @GetMapping("/{assetId}/profitOrLoss")
    public ResponseEntity<Integer> calculateProfitOrLoss(@PathVariable Long assetId) {
        Integer profitOrLoss = assetService.calculateProfitOrLoss(assetId);
        return ResponseEntity.ok(profitOrLoss);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/{assetId}/allocate")
    public ResponseEntity<String> allocateAsset(@PathVariable Long assetId, @RequestParam String allocatedTo) {
        assetService.allocateAsset(assetId, allocatedTo);
        return ResponseEntity.ok("Asset allocated successfully.");
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('OFFICER')")
    @PostMapping("/{assetId}/request")
    public ResponseEntity<AssetRequest> requestAsset(@PathVariable Long assetId, @RequestParam Long userId) {
        AssetRequest assetRequest = assetService.requestAsset(assetId, userId);
        return ResponseEntity.ok(assetRequest);

    }
    @PreAuthorize("hasRole('MANAGER') or hasRole('OFFICER')")
    @GetMapping("/requests/my")
    public ResponseEntity<List<AssetRequest>> getMyAssetRequests(@RequestParam Long userId) {
        List<AssetRequest> assetRequests = assetService.getAssetRequestsByUserId(userId);
        return ResponseEntity.ok(assetRequests);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/requests")
    public ResponseEntity<List<AssetRequest>> getAllAssetRequests() {
        List<AssetRequest> assetRequests = assetService.getAllAssetRequests();
        return ResponseEntity.ok(assetRequests);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<String> acceptAssetRequest(@PathVariable Long requestId) {
        // Logic to accept the asset request
        assetService.acceptAssetRequest(requestId);
        return ResponseEntity.ok("Asset request accepted successfully.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<String> rejectAssetRequest(@PathVariable Long requestId) {
        // Logic to reject the asset request
        assetService.rejectAssetRequest(requestId);
        return ResponseEntity.ok("Asset request rejected successfully.");
    }
    @GetMapping("/all")
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }

    @GetMapping("/unallocated")
    public ResponseEntity<List<Asset>> getAllUnallocatedAssets() {
        List<Asset> unallocatedAssets = assetService.getAllUnallocatedAssets();
        return ResponseEntity.ok(unallocatedAssets);
    }
}
