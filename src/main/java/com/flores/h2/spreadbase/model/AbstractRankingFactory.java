package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.util.TypeHierarchy;

public class AbstractRankingFactory implements IRankEvaluator {

	protected TypeHierarchy hierarchy;
	public AbstractRankingFactory(TypeHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	@Override
	public TypeHierarchy getTypeHierarchy() {
		return hierarchy;
	}
}
