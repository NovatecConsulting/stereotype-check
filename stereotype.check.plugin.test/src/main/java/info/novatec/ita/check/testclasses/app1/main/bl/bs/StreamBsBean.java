package info.novatec.ita.check.testclasses.app1.main.bl.bs;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import info.novatec.ita.check.testclasses.app1.shared.bl.bs.SampleBs;
import info.novatec.ita.check.testclasses.core.fwk.common.exception.interceptor.BusinessServiceExceptionInterceptor;

@Stateless
@Interceptors(BusinessServiceExceptionInterceptor.class)
public class StreamBsBean implements SampleBs {

	protected static FieldDescriptor prefixed(String prefix, FieldDescriptor descriptor) {
		FieldDescriptor prefixed = fieldWithPath(prefix + descriptor.getPath());
		prefixed.type(descriptor.getType());
		prefixed.description(descriptor.getDescription());
		if (descriptor.isOptional()) {
			prefixed.optional();
		}
		if (descriptor.isIgnored()) {
			prefixed.ignored();
		}
		//Works with stereotype check
		//		for (Entry<String, Object> attributes : descriptor.getAttributes().entrySet()) {
		//			prefixed.attributes(key(attributes.getKey()).value(attributes.getValue()));
		//		}
		//Nullpointer with stereotype check
		descriptor.getAttributes().forEach((key, value) -> {
			prefixed.attributes(key(key).value(value));
		});
		return prefixed;
	}
}
