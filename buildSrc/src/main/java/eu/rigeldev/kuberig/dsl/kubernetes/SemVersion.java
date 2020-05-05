package eu.rigeldev.kuberig.dsl.kubernetes;

import java.util.Objects;

public class SemVersion {
    public static SemVersion fromTagName(String tagName) {
        String tagSemVersionPart;
        if (tagName.startsWith("v")) {
            tagSemVersionPart = tagName.substring(1);
        } else {
            tagSemVersionPart = tagName;
        }

        final String[] parts = tagSemVersionPart.split("\\.");

        if (parts.length != 3) {
            return null;
        }

        return new SemVersion(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
        );
    }

    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    public SemVersion(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemVersion that = (SemVersion) o;
        return majorVersion == that.majorVersion &&
                minorVersion == that.minorVersion &&
                patchVersion == that.patchVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion);
    }

    public boolean isHigher(SemVersion otherVersion) {
        if (this.majorVersion > otherVersion.majorVersion) {
            return true;
        } else if (this.majorVersion == otherVersion.majorVersion) {
            if (this.minorVersion > otherVersion.minorVersion) {
                return true;
            } else if (this.minorVersion == otherVersion.minorVersion) {
                return this.patchVersion > otherVersion.patchVersion;
            }
        }

        return false;
    }
}
