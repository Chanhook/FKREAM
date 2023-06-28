package com.flab.fkream.item;

import com.flab.fkream.brand.Brand;
import com.flab.fkream.brand.BrandService;
import com.flab.fkream.error.exception.NoDataFoundException;
import com.flab.fkream.kafka.KafkaMessageSender;
import com.flab.fkream.kafka.KafkaTopic;
import com.flab.fkream.search.Trie;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ItemService {

    private final ItemMapper itemMapper;
    private final BrandService brandService;
    private final KafkaMessageSender messageSender;
    private final Trie trie;

    public void addItem(Item itemInfo) {
        itemInfo.setCreatedAtToNow();
        itemMapper.save(itemInfo);
        trie.insert(itemInfo);
        messageSender.send(KafkaTopic.ITEM_TOPIC, itemInfo);
    }

    public Item findOne(Long itemId) {
        Item item = itemMapper.findOne(itemId);
        if (item == null) {
            throw new NoDataFoundException();
        }
        Brand brand = brandService.findOne(item.getBrand().getId());
        item.setBrand(brand);
        return item;
    }


    public List<Item> findAll() {
        List<Item> items = itemMapper.findAll();
        if (items.size() == 0) {
            throw new NoDataFoundException();
        }
        for (Item item : items) {
            item.setBrand(brandService.findOne(item.getBrand().getId()));
        }
        return items;
    }

    public void update(Item itemInfo) {
        itemInfo.setModifiedAtToNow();
        itemMapper.update(itemInfo);
    }

    public void delete(Long id) {
        itemMapper.delete(id);

    }

    public List<Item> findByBrand(Brand brand) {
        return itemMapper.findByBrand(brand);
    }
}
