package com.fsse.parser.dao;

import com.fsse.model.FsItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface FsItemRepository extends JpaRepository<FsItem, BigInteger> {
}
