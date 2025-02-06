package com.scit.iLog.service;
import org.springframework.stereotype.Service;
import com.scit.iLog.repository.ChildRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChildService
	{
		private final ChildRepository childRepository;
	}
