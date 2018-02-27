package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;

public class EmptyModel extends Model {

	@Override
	protected void doConvert(Dictionary dictionary,
			List<ModelElement> elements, List<ModelError> errors) {
	}

	@Override
	public List<Section> getSectionDefs() {
		return new ArrayList<Model.Section>();
	}

	@Override
	public String getExtension() {
		return ""; //$NON-NLS-1$
	}
}
