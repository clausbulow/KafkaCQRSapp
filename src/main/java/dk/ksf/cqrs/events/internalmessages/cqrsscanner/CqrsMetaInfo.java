package dk.ksf.cqrs.events.internalmessages.cqrsscanner;

import dk.ksf.cqrs.CqrsProperties;
import dk.ksf.cqrs.events.annotations.*;
import dk.ksf.cqrs.events.model.AggregateTypes;
import dk.ksf.cqrs.exceptions.ExceptionConsumer;
import dk.ksf.cqrs.exceptions.MessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


@Service
@Slf4j
public class CqrsMetaInfo {
    final CqrsProperties props;
    private final List<Class> classAnnotationsOfInterest = Arrays.asList(
            Aggregate.class, Perspective.class
    );


    private final Map<Class<?>, String> eventClassesToNames = new HashMap<>();
    private final Map<String, Class<?>> eventNamesToClasses = new HashMap<>();

    private final Map<Class<?>, AggregateMetainfo> aggregates = new HashMap<>();
    private final Map<Class<?>, PerspectiveMetainfo> perspectives = new HashMap<>();

    private final Map<Class<?>, AggregateTypes> aggregateClassesToAggregateType = new HashMap<>();
    private final Map<AggregateTypes, Class<?>> aggregateTypeToClass = new HashMap<>();

    private final Map<Class<?>, Field> aggregateIdentfierFieldsToClass = new HashMap<>();

    private final Map<Class<?>, Field> classToKeyField = new HashMap<>();

    public CqrsMetaInfo(CqrsProperties props) {
        this.props = props;
    }


    public String getEventName(Class<?> klientOprettetObjectClass) {
        return eventClassesToNames.get(klientOprettetObjectClass);
    }

    public Class<?> getEventClass(String eventNavn) {
        return eventNamesToClasses.get(eventNavn);
    }

    public void init() throws Exception {
        scanForBusinessObjects(props.getEventobjectsPackages());
        scanForContainers(props.getEventobjectsPackages());
    }

    public void init(List<String> basePackage) throws Exception {
        scanForBusinessObjects(basePackage);
        scanForContainers(basePackage);
    }
    public void scanForContainers(List<String> basePackages) throws Exception {
        final ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        classAnnotationsOfInterest.forEach(clazz -> scanner.addIncludeFilter(new AnnotationTypeFilter(clazz)));
        basePackages.forEach(ExceptionConsumer.wrapper(basePackage -> {
        final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
        for (BeanDefinition definition : candidateComponents) {
            Class clazz = Class.forName(definition.getBeanClassName());
            scanContainerForContainerType(clazz);
          }
        }));
        scanContainers();
        log.info("scanning done");


    }


    private void scanContainers() {
        scanAggregates();
        scanPerspectives();
    }

    private void scanPerspectives() {
        this.perspectives.forEach((clazz, perspectiveMetainfo) -> {
            scanForHandlers(clazz, perspectiveMetainfo, Arrays.asList(EventHandler.class));

        });
    }

    private void scanAggregates() {
        this.aggregates.forEach((clazz, aggregateMetainfo) -> {
            ReflectionUtils.doWithLocalFields(clazz, field -> {
                AggregateIdentifier annotation = field.getAnnotation(AggregateIdentifier.class);
                if (annotation != null) {
                    ReflectionUtils.makeAccessible(field);
                    aggregateMetainfo.setKeyField(field);
                }
            });
            scanForHandlers(clazz, aggregateMetainfo, Arrays.asList(
                    CommandHandler.class, EventSourcingHandler.class, EventHandler.class));

        });
    }

    private void scanForHandlers(Class clazz, HandlerMetainfoHolder metainfoHolder, List<Class<? extends Annotation>> annotationsOnInterest) {
            ReflectionUtils.doWithMethods(clazz, method -> {
                annotationsOnInterest.forEach(ExceptionConsumer.wrapper(annotationClass -> {
                    Annotation annotation = AnnotationUtils.findAnnotation(method, annotationClass);
                    if (annotation != null) {
                        metainfoHolder.getHandlers().put(method,HandlerMetaInfo.builder().method(method).annotation(annotation).build());
                        if (annotation instanceof CommandHandler){
                            scanCommandHandler(method, metainfoHolder);
                        }
                        if (annotation instanceof EventSourcingHandler){
                            scanEventSourcingHandler(method, metainfoHolder);
                        }
                    }
                }));
            });
    }

    protected void scanEventSourcingHandler (Method method, HandlerMetainfoHolder metainfoHolder){
        Class<?> parameterType = method.getParameterTypes()[1];
        ReflectionUtils.doWithLocalFields(parameterType, field -> {
            AggregateIdentifier annotation = AnnotationUtils.findAnnotation(field, AggregateIdentifier.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                registerAggrateIdentifer(parameterType, field);
            }
        });

    }
    protected void scanCommandHandler (Method method, HandlerMetainfoHolder metainfoHolder){
        Class<?> parameterType = method.getParameterTypes()[1];
        ReflectionUtils.doWithLocalFields(parameterType, field -> {
            TargetAggregateIdentifier annotation = AnnotationUtils.findAnnotation(field, TargetAggregateIdentifier.class);
            if (annotation != null) {
                ReflectionUtils.makeAccessible(field);
                this.registerAggrateIdentifer(parameterType, field);
            }
        });
    }

    private void scanContainerForContainerType(Class clazz) {
        final Aggregate aggregateAnnotation = (Aggregate) clazz.getAnnotation(Aggregate.class);
        if (aggregateAnnotation != null) {
            this.aggregates.put(clazz, AggregateMetainfo.builder().annotation(aggregateAnnotation).build());
            registerAggregate(aggregateAnnotation.aggregateType(), clazz);
        } else {
            final Perspective perspectiveAnnotation = (Perspective) clazz.getAnnotation(Perspective.class);
            if (perspectiveAnnotation != null) {
                this.perspectives.put(clazz, PerspectiveMetainfo.builder().annotation(perspectiveAnnotation).build());
            }
        }

    }


    private void scanForBusinessObjects(List<String> basePackages) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner;
        scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(BusinessObject.class));
        //Maybe use: Collection<Object> containers = context.getBeansWithAnnotation(Aggregate.class).values();
        basePackages.forEach(ExceptionConsumer.wrapper(basePackage -> {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition definition : candidateComponents) {
                System.out.println(definition.getBeanClassName());
                Class clazz = Class.forName(definition.getBeanClassName());
                //new TypeDescriptor.OfMethod(clazz.getMethod("test",clazz));
                BusinessObject annotation = AnnotationUtils.findAnnotation(clazz, BusinessObject.class);
                String eventName = (String) AnnotationUtils.getValue(annotation, "eventName");
                eventNamesToClasses.put(eventName, clazz);
                eventClassesToNames.put(clazz, eventName);
            }
        }));
    }



    public void registerAggregate(AggregateTypes aggregateType, Class clazz) {
        this.aggregateTypeToClass.put(aggregateType, clazz);
        this.aggregateClassesToAggregateType.put(clazz, aggregateType);
    }

    public Collection<AggregateTypes> getAggregatesSupportedInApplication() {
        return this.aggregateClassesToAggregateType.values();
    }

    public AggregateTypes getAggregateType(Class<?> aClass) {
        return this.aggregateClassesToAggregateType.get(aClass);
    }

    public void registerKeyField(Class containerClass, Field field) {
        classToKeyField.put(containerClass, field);
    }

    public Field getKeyField(Class containerClass) throws Exception {
        AggregateMetainfo aggregateMetainfo = this.aggregates.get(containerClass);
        if (aggregateMetainfo != null){
            return aggregateMetainfo.getKeyField();
        }
        throw new MessageException("Not able to retrieve keyfield from class "+containerClass);
    }

    public void registerAggrateIdentifer(Class parameterType, Field field) {
        if (!this.aggregateIdentfierFieldsToClass.containsKey(parameterType)) {
            this.aggregateIdentfierFieldsToClass.put(parameterType, field);
        }
    }

    public Field getAggregateIdentifierFromClass(Class clazz) {
        return this.aggregateIdentfierFieldsToClass.get(clazz);
    }

    public Map<Class<?>, AggregateMetainfo> getAggregates() {
        return aggregates;
    }

    public Map<Class<?>, PerspectiveMetainfo> getPerspectives() {
        return perspectives;
    }
}
