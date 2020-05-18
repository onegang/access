<template>
  <v-form v-model="valid">
    <v-container>
      <v-row> 
        <v-col cols="12" sm="6" md="4">
          <v-menu
            v-model="effectiveDateMenu"
            :close-on-content-click="false"
            :nudge-right="40"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="effectiveDate"
              label="Effective on"
              prepend-icon="mdi-calendar"
              hint="YYYY-MM-DD format"
              v-on="on"
            ></v-text-field>
            </template>
            <v-date-picker v-model="effectiveDate" @input="effectiveDateMenu = false"></v-date-picker>
          </v-menu>
        </v-col>

        <v-col cols="12" sm="6" md="4">
          <v-menu
            v-model="expiryDateMenu"
            :close-on-content-click="false"
            :nudge-right="40"
            transition="scale-transition"
            offset-y
            min-width="290px"
          >
          <template v-slot:activator="{ on }">
            <v-text-field
              v-model="expiryDate"
              label="Expired on (Optional)"
              prepend-icon="mdi-calendar"
              hint="YYYY-MM-DD format"
              v-on="on"
              clearable
            ></v-text-field>
            </template>
            <v-date-picker v-model="expiryDate" @input="expiryDateMenu = false"></v-date-picker>
          </v-menu>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12" md="12">
          <v-textarea
            v-model="comments"
            label="Comments"
            clearable
          ></v-textarea>
        </v-col>
      </v-row>
    </v-container>
  </v-form>
</template>

<script>

export default {
  data: () => ({
    effectiveDate: new Date().toISOString().substr(0, 10),
    expiryDate: null,
    effectiveDateMenu: false,
    expiryDateMenu: false,
    comments: null,
    valid: false,
    }),
};
</script>

<style scoped>
  
</style>
