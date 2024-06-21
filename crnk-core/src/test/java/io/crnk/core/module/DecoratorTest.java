package io.crnk.core.module;

import io.crnk.core.engine.registry.ResourceRegistryAware;
import io.crnk.core.mock.models.Schedule;
import io.crnk.core.mock.models.Task;
import io.crnk.core.mock.repository.ScheduleRepository;
import io.crnk.core.repository.RelationshipRepository;
import io.crnk.core.repository.decorate.WrappedRelationshipRepository;
import io.crnk.core.repository.decorate.WrappedResourceRepository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DecoratorTest {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testDecoratedResourceRepositoryBase() {
        ScheduleRepository repository = Mockito.mock(ScheduleRepository.class);
        WrappedResourceRepository<Schedule, Long> decorator = new WrappedResourceRepository<>(repository);
        Assert.assertSame(repository, decorator.getWrappedObject());

        decorator.create(null);
        Mockito.verify(repository, Mockito.times(1)).create(Mockito.any());

        decorator.delete(null);
        Mockito.verify(repository, Mockito.times(1)).delete(Mockito.any());

        decorator.findAll(null);
        Mockito.verify(repository, Mockito.times(1)).findAll(Mockito.any());

        decorator.findAll(null, null);
        Mockito.verify(repository, Mockito.times(1)).findAll(Mockito.any(), Mockito.any());

        decorator.getResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getResourceClass();

        Schedule schedule = Mockito.mock(Schedule.class);
        decorator.save(schedule);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.eq(schedule));

        decorator.findOne(null, null);
        Mockito.verify(repository, Mockito.times(1)).findOne(Mockito.any(), Mockito.any());
    }

    interface RegistryAwareResourceRepository extends ScheduleRepository, ResourceRegistryAware {

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testDecoratedRelationshipRepositoryBase() {
        RelationshipRepository<Schedule, Long, Task, Long> repository = Mockito.mock(RelationshipRepository.class);
        WrappedRelationshipRepository<Schedule, Long, Task, Long> decorator = new WrappedRelationshipRepository(repository);

        decorator.findManyTargets(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).findManyTargets(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.findOneTarget(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).findOneTarget(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.setRelation(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).setRelation(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.addRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).addRelations(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.setRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).setRelations(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.removeRelations(null, null, null);
        Mockito.verify(repository, Mockito.times(1)).removeRelations(Mockito.any(), Mockito.any(),
                Mockito.any());

        decorator.getTargetResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getTargetResourceClass();

        decorator.getSourceResourceClass();
        Mockito.verify(repository, Mockito.times(1)).getSourceResourceClass();
    }
}
