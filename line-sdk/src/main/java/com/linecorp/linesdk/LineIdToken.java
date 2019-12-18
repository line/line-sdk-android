package com.linecorp.linesdk;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

import static com.linecorp.linesdk.utils.ParcelUtils.readDate;
import static com.linecorp.linesdk.utils.ParcelUtils.writeDate;

/**
 * Represents an ID token that contains the user's information. <br/>
 * For more information about each field, please refer to: <br/>
 * <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">OpenID Connect 1.0: ID Token</a> <br/>
 * <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">OpenID Connect 1.0: Standard Claims</a>
 */
public class LineIdToken implements Parcelable {

    public static final Creator<LineIdToken> CREATOR = new Creator<LineIdToken>() {
        @Override
        public LineIdToken createFromParcel(final Parcel in) {
            return new LineIdToken(in);
        }

        @Override
        public LineIdToken[] newArray(final int size) {
            return new LineIdToken[size];
        }
    };

    // raw string of the ID Token
    @NonNull
    private final String rawString;

    @NonNull
    private final String issuer;
    // encrypted mid
    @NonNull
    private final String subject;
    // channel id
    @NonNull
    private final String audience;
    // expires at
    @NonNull
    private final Date expiresAt;
    // issued at
    @NonNull
    private final Date issuedAt;
    @Nullable
    private final Date authTime;
    // the same value as in the authentication request
    @Nullable
    private final String nonce;
    // Authentication Methods References
    // List of strings that are identifiers for authentication methods used in the authentication.
    @Nullable
    private final List<String> amr;
    @Nullable
    private final String name;
    @Nullable
    private final String picture;
    @Nullable
    private final String phoneNumber;
    @Nullable
    private final String email;
    @Nullable
    private final String gender;
    @Nullable
    private final String birthdate;
    @Nullable
    private final Address address;
    // Given name(s) or first name(s) of the End-User
    @Nullable
    private final String givenName;
    // Private Claim:
    // Pronunciation of givenName, usually used as Furigana of Japanese name
    @Nullable
    private final String givenNamePronunciation;
    // Middle name(s) of the End-User
    @Nullable
    private final String middleName;
    // Surname(s) or last name(s) of the End-User
    @Nullable
    private final String familyName;
    // Private Claim:
    // Pronunciation of familyName, usually used as Furigana of Japanese name
    @Nullable
    private final String familyNamePronunciation;

    private LineIdToken(final Builder builder) {
        rawString = builder.rawString;
        issuer = builder.issuer;
        subject = builder.subject;
        audience = builder.audience;
        expiresAt = builder.expiresAt;
        issuedAt = builder.issuedAt;
        authTime = builder.authTime;
        nonce = builder.nonce;
        amr = builder.amr;
        name = builder.name;
        picture = builder.picture;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
        gender = builder.gender;
        birthdate = builder.birthdate;
        address = builder.address;
        givenName = builder.givenName;
        givenNamePronunciation = builder.givenNamePronunciation;
        middleName = builder.middleName;
        familyName = builder.familyName;
        familyNamePronunciation = builder.familyNamePronunciation;
    }

    private LineIdToken(@NonNull final Parcel in) {
        rawString = in.readString();
        issuer = in.readString();
        subject = in.readString();
        audience = in.readString();
        expiresAt = readDate(in);
        issuedAt = readDate(in);
        authTime = readDate(in);
        nonce = in.readString();
        amr = in.createStringArrayList();
        name = in.readString();
        picture = in.readString();
        phoneNumber = in.readString();
        email = in.readString();
        gender = in.readString();
        birthdate = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        givenName = in.readString();
        givenNamePronunciation = in.readString();
        middleName = in.readString();
        familyName = in.readString();
        familyNamePronunciation = in.readString();
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(rawString);
        dest.writeString(issuer);
        dest.writeString(subject);
        dest.writeString(audience);
        writeDate(dest, expiresAt);
        writeDate(dest, issuedAt);
        writeDate(dest, authTime);
        dest.writeString(nonce);
        dest.writeStringList(amr);
        dest.writeString(name);
        dest.writeString(picture);
        dest.writeString(phoneNumber);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(birthdate);
        dest.writeParcelable(address, flags);
        dest.writeString(givenName);
        dest.writeString(givenNamePronunciation);
        dest.writeString(middleName);
        dest.writeString(familyName);
        dest.writeString(familyNamePronunciation);
    }

    /**
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Gets the raw string of the ID Token.
     *
     * @return The raw string of the ID Token.
     */
    @NonNull
    public String getRawString() {
        return rawString;
    }

    /**
     * Gets the ID of the issuer of the ID token.
     * @return The URL of the LINE Platform, https://access.line.me.
     */
    @NonNull
    public String getIssuer() {
        return issuer;
    }

    /**
     * Gets the user ID of the user that the ID token is generated for.
     * @return The user ID.
     */
    @NonNull
    public String getSubject() {
        return subject;
    }

    /**
     * Gets the channel ID.
     * @return The channel ID.
     */
    @NonNull
    public String getAudience() {
        return audience;
    }

    /**
     * Gets the expiration time of the ID Token.
     * @return The expiration time of the ID Token in UNIX time.
     */
    @NonNull
    public Date getExpiresAt() {
        return expiresAt;
    }

    /**
     * Gets the time when the ID token was generated.
     * @return The generation time of the ID token in UNIX time.
     */
    @NonNull
    public Date getIssuedAt() {
        return issuedAt;
    }

    /**
     * Gets the time when the user authentication occurred.
     * @return The authentication time of the user in UNIX time.
     */
    @Nullable
    public Date getAuthTime() {
        return authTime;
    }

    /**
     * @hide
     */
    @Nullable
    public String getNonce() {
        return nonce;
    }

    /**
     * Get the Authentication Methods References.
     * @return the Authentication Methods References. <br></br>
     * List of strings that are identifiers for authentication methods used in the authentication.
     */
    @Nullable
    public List<String> getAmr() {
        return amr;
    }

    /**
     * Gets the user's display name.
     * @return The user's display name.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Gets the user's profile image URL.
     * @return The user's profile image URL.
     */
    @Nullable
    public String getPicture() {
        return picture;
    }

    /**
     * @hide
     */
    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the user's email address.
     * @return
     */
    @Nullable
    public String getEmail() {
        return email;
    }

    /**
     * @hide
     */
    @Nullable
    public String getGender() {
        return gender;
    }

    /**
     * @hide
     */
    @Nullable
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * @hide
     */
    @Nullable
    public Address getAddress() {
        return address;
    }

    /**
     * @hide
     */
    @Nullable
    public String getGivenName() {
        return givenName;
    }

    /**
     * @hide
     */
    @Nullable
    public String getGivenNamePronunciation() {
        return givenNamePronunciation;
    }

    /**
     * @hide
     */
    @Nullable
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @hide
     */
    @Nullable
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @hide
     */
    @Nullable
    public String getFamilyNamePronunciation() {
        return familyNamePronunciation;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final LineIdToken that = (LineIdToken) o;

        if (!rawString.equals(that.rawString)) { return false; }
        if (!issuer.equals(that.issuer)) { return false; }
        if (!subject.equals(that.subject)) { return false; }
        if (!audience.equals(that.audience)) { return false; }
        if (!expiresAt.equals(that.expiresAt)) { return false; }
        if (!issuedAt.equals(that.issuedAt)) { return false; }
        if (authTime != null ? !authTime.equals(that.authTime) : that.authTime != null) { return false; }
        if (nonce != null ? !nonce.equals(that.nonce) : that.nonce != null) { return false; }
        if (amr != null ? !amr.equals(that.amr) : that.amr != null) { return false; }
        if (name != null ? !name.equals(that.name) : that.name != null) { return false; }
        if (picture != null ? !picture.equals(that.picture) : that.picture != null) { return false; }
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) {
            return false;
        }
        if (email != null ? !email.equals(that.email) : that.email != null) { return false; }
        if (gender != null ? !gender.equals(that.gender) : that.gender != null) { return false; }
        if (birthdate != null ? !birthdate.equals(that.birthdate) : that.birthdate != null) { return false; }
        if (address != null ? !address.equals(that.address) : that.address != null) { return false; }
        if (givenName != null ? !givenName.equals(that.givenName) : that.givenName != null) { return false; }
        if (givenNamePronunciation != null ? !givenNamePronunciation.equals(that.givenNamePronunciation) :
            that.givenNamePronunciation != null) { return false; }
        if (middleName != null ? !middleName.equals(that.middleName) : that.middleName != null) {
            return false;
        }
        if (familyName != null ? !familyName.equals(that.familyName) : that.familyName != null) {
            return false;
        }
        return familyNamePronunciation != null ? familyNamePronunciation.equals(that.familyNamePronunciation) :
               that.familyNamePronunciation == null;
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = rawString.hashCode();
        result = 31 * result + issuer.hashCode();
        result = 31 * result + subject.hashCode();
        result = 31 * result + audience.hashCode();
        result = 31 * result + expiresAt.hashCode();
        result = 31 * result + issuedAt.hashCode();
        result = 31 * result + (authTime != null ? authTime.hashCode() : 0);
        result = 31 * result + (nonce != null ? nonce.hashCode() : 0);
        result = 31 * result + (amr != null ? amr.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthdate != null ? birthdate.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (givenNamePronunciation != null ? givenNamePronunciation.hashCode() : 0);
        result = 31 * result + (middleName != null ? middleName.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (familyNamePronunciation != null ? familyNamePronunciation.hashCode() : 0);
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineIdToken{" +
               "rawString='" + rawString + '\'' +
               ", issuer='" + issuer + '\'' +
               ", subject='" + subject + '\'' +
               ", audience='" + audience + '\'' +
               ", expiresAt=" + expiresAt +
               ", issuedAt=" + issuedAt +
               ", authTime=" + authTime +
               ", nonce='" + nonce + '\'' +
               ", amr=" + amr +
               ", name='" + name + '\'' +
               ", picture='" + picture + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", email='" + email + '\'' +
               ", gender='" + gender + '\'' +
               ", birthdate='" + birthdate + '\'' +
               ", address=" + address +
               ", givenName='" + givenName + '\'' +
               ", givenNamePronunciation='" + givenNamePronunciation + '\'' +
               ", middleName='" + middleName + '\'' +
               ", familyName='" + familyName + '\'' +
               ", familyNamePronunciation='" + familyNamePronunciation + '\'' +
               '}';
    }

    /**
     * @hide
     */
    public static final class Builder {
        private String rawString;
        private String issuer;
        private String subject;
        private String audience;
        private Date expiresAt;
        private Date issuedAt;
        private Date authTime;
        private String nonce;
        private List<String> amr;
        private String name;
        private String picture;
        private String phoneNumber;
        private String email;
        private String gender;
        private String birthdate;
        private Address address;
        private String givenName;
        private String givenNamePronunciation;
        private String middleName;
        private String familyName;
        private String familyNamePronunciation;

        public Builder() {}

        public Builder rawString(final String rawString) {
            this.rawString = rawString;
            return this;
        }

        public Builder issuer(final String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder subject(final String subject) {
            this.subject = subject;
            return this;
        }

        public Builder audience(final String audience) {
            this.audience = audience;
            return this;
        }

        public Builder expiresAt(final Date expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder issuedAt(final Date issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Builder authTime(final Date authTime) {
            this.authTime = authTime;
            return this;
        }

        public Builder nonce(final String nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder amr(final List<String> amr) {
            this.amr = amr;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder picture(final String picture) {
            this.picture = picture;
            return this;
        }

        public Builder phoneNumber(final String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public Builder gender(final String gender) {
            this.gender = gender;
            return this;
        }

        public Builder birthdate(final String birthdate) {
            this.birthdate = birthdate;
            return this;
        }

        public Builder address(final Address address) {
            this.address = address;
            return this;
        }

        public Builder givenName(final String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder givenNamePronunciation(final String givenNamePronunciation) {
            this.givenNamePronunciation = givenNamePronunciation;
            return this;
        }

        public Builder middleName(final String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder familyName(final String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder familyNamePronunciation(final String familyNamePronunciation) {
            this.familyNamePronunciation = familyNamePronunciation;
            return this;
        }

        public LineIdToken build() {
            return new LineIdToken(this);
        }
    }

    /**
     * @hide
     */
    public static class Address implements Parcelable {
        public static final Creator<Address> CREATOR = new Creator<Address>() {
            @Override
            public Address createFromParcel(final Parcel in) {
                return new Address(in);
            }

            @Override
            public Address[] newArray(final int size) {
                return new Address[size];
            }
        };

        @Nullable
        private final String streetAddress;

        @Nullable
        private final String locality;

        @Nullable
        private final String region;

        @Nullable
        private final String postalCode;

        @Nullable
        private final String country;

        private Address(final Builder builder) {
            streetAddress = builder.streetAddress;
            locality = builder.locality;
            region = builder.region;
            postalCode = builder.postalCode;
            country = builder.country;
        }

        private Address(@NonNull final Parcel in) {
            streetAddress = in.readString();
            locality = in.readString();
            region = in.readString();
            postalCode = in.readString();
            country = in.readString();
        }

        /**
         * @hide
         */
        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(streetAddress);
            dest.writeString(locality);
            dest.writeString(region);
            dest.writeString(postalCode);
            dest.writeString(country);
        }

        /**
         * @hide
         */
        @Override
        public int describeContents() {
            return 0;
        }

        @Nullable
        public String getStreetAddress() {
            return streetAddress;
        }

        @Nullable
        public String getLocality() {
            return locality;
        }

        @Nullable
        public String getRegion() {
            return region;
        }

        @Nullable
        public String getPostalCode() {
            return postalCode;
        }

        @Nullable
        public String getCountry() {
            return country;
        }

        /**
         * @hide
         */
        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final Address that = (Address) o;

            if (streetAddress != null ? !streetAddress.equals(that.streetAddress) :
                that.streetAddress != null) {
                return false;
            }
            if (locality != null ? !locality.equals(that.locality) : that.locality != null) { return false; }
            if (region != null ? !region.equals(that.region) : that.region != null) { return false; }
            if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
                return false;
            }
            return country != null ? country.equals(that.country) : that.country == null;
        }

        /**
         * @hide
         */
        @Override
        public int hashCode() {
            int result = streetAddress != null ? streetAddress.hashCode() : 0;
            result = 31 * result + (locality != null ? locality.hashCode() : 0);
            result = 31 * result + (region != null ? region.hashCode() : 0);
            result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
            result = 31 * result + (country != null ? country.hashCode() : 0);
            return result;
        }

        /**
         * @hide
         */
        @Override
        public String toString() {
            return "Address{" +
                   "streetAddress='" + streetAddress + '\'' +
                   ", locality='" + locality + '\'' +
                   ", region='" + region + '\'' +
                   ", postalCode='" + postalCode + '\'' +
                   ", country='" + country + '\'' +
                   '}';
        }

        /**
         * @hide
         */
        public static final class Builder {
            private String streetAddress;
            private String locality;
            private String region;
            private String postalCode;
            private String country;

            public Builder() {}

            public Builder streetAddress(final String streetAddress) {
                this.streetAddress = streetAddress;
                return this;
            }

            public Builder locality(final String locality) {
                this.locality = locality;
                return this;
            }

            public Builder region(final String region) {
                this.region = region;
                return this;
            }

            public Builder postalCode(final String postalCode) {
                this.postalCode = postalCode;
                return this;
            }

            public Builder country(final String country) {
                this.country = country;
                return this;
            }

            public Address build() {
                return new Address(this);
            }
        }
    }
}
