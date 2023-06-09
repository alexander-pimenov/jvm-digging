package main.examples.model;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

@Table("record_package")
public class RecordPackage {
    @Id private final Long recordPackageId;
    private final String name;

    @MappedCollection(idColumn = "record_package_id")
    private final Set<Record> records;

    @PersistenceCreator
    private RecordPackage(Long recordPackageId, String name, Set<Record> records) {
        this.recordPackageId = recordPackageId;
        this.name = name;
        this.records = records;
    }

    public RecordPackage(String name, Set<Record> records) {
        this.recordPackageId = null;
        this.name = name;
        this.records = records;
    }

    public Long getRecordPackageId() {
        return recordPackageId;
    }

    public String getName() {
        return name;
    }

    public Set<Record> getRecords() {
        return records;
    }

    @Override
    public String toString() {
        return "RecordPackage{"
                + "recordPackageId="
                + recordPackageId
                + ", name='"
                + name
                + '\''
                + ", recordList="
                + records
                + '}';
    }
}
