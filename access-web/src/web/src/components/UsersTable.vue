<template>
  <v-data-iterator
    :items="items"
    :items-per-page.sync="itemsPerPage"
    :page="page"
    :search="search"
    :sort-by="sortBy.toLowerCase()"
    :sort-desc="sortDesc"
    hide-default-footer
  >
  <template v-slot:header>
    <v-text-field
      v-model="search"
      clearable
      outlined
      label="Find users..."
      prepend-inner-icon="mdi-magnify"
    ></v-text-field>
    </template>
    <template v-slot:default="props">
      <v-row>
        <v-col
          v-for="item in props.items"
          :key="item.name"
          cols="12"
          sm="6"
          md="4"
          lg="3"
        >
          <v-card ripple @click="item.selected = !item.selected">
            <v-card-title class="subheading font-weight-bold">
              <v-icon class="pr-2" v-if="item.selected">mdi-checkbox-marked-outline</v-icon>
              {{ item.name }}
            </v-card-title>
            <!-- <v-card-actions>
              <v-spacer></v-spacer>
              <v-btn icon>
                <v-icon>mdi-checkbox-marked-outline</v-icon>
              </v-btn>
            </v-card-actions> -->
            <v-divider></v-divider>

            <v-list dense>
              <v-list-item
                v-for="(key, index) in filteredKeys"
                :key="index"
              >
                <v-list-item-content :class="{ 'blue--text':
                  sortBy === key }">{{ key }}:</v-list-item-content>
                <v-list-item-content class="align-end" :class="{ 'blue--text': sortBy === key }">
                  {{ item[key.toLowerCase()] }}</v-list-item-content>
              </v-list-item>
            </v-list>
          </v-card>
        </v-col>
      </v-row>
    </template>

    <template v-slot:footer>
      <v-row class="mt-2" align="center" justify="center">
        <span class="grey--text">Items per page</span>
        <v-menu offset-y>
          <template v-slot:activator="{ on }">
            <v-btn
              dark
              text
              color="primary"
              class="ml-2"
              v-on="on"
            >
              {{ itemsPerPage }}
              <v-icon>mdi-chevron-down</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="(number, index) in itemsPerPageArray"
              :key="index"
              @click="updateItemsPerPage(number)"
            >
              <v-list-item-title>{{ number }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-spacer></v-spacer>

        <span
          class="mr-4
          grey--text"
        >
          Page {{ page }} of {{ numberOfPages }}
        </span>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="mr-1"
          @click="formerPage"
        >
          <v-icon>mdi-chevron-left</v-icon>
        </v-btn>
        <v-btn
          fab
          dark
          color="blue darken-3"
          class="ml-1"
          @click="nextPage"
        >
          <v-icon>mdi-chevron-right</v-icon>
        </v-btn>
      </v-row>
    </template>
  </v-data-iterator>
</template>

<script>
export default {
  // name: 'UsersTable',
  // props: {
  //   msg: String,
  // },
  data() {
    return {
      itemsPerPageArray: [4, 8, 12],
      search: '',
      filter: {},
      sortDesc: false,
      page: 1,
      itemsPerPage: 4,
      sortBy: 'name',
      keys: [
        'Name',
        'Roles',
        'Active',
      ],
      items: [
        {
          name: 'John Lee, 1001',
          roles: 'Administrator',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Mary Jane, 1002',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Jane Doe, 1003',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Cooper Down, 2001',
          roles: 'Researcher',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Cooper Down Jr, 2010',
          roles: 'Researcher',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Jelly Jim, 3001',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Tom Holly, 3002',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Kitkat Teck, 3003',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Kylie Key, 3012',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
        {
          name: 'Morthone To, 3020',
          roles: 'Analyst',
          active: 'Yes',
          selected: false,
        },
      ],
    };
  },
  computed: {
    numberOfPages() {
      return Math.ceil(this.items.length / this.itemsPerPage);
    },
    filteredKeys() {
      return this.keys.filter((key) => (key !== 'Name'));
    },
  },
  methods: {
    nextPage() {
      if (this.page + 1 <= this.numberOfPages) this.page += 1;
    },
    formerPage() {
      if (this.page - 1 >= 1) this.page -= 1;
    },
    updateItemsPerPage(number) {
      this.itemsPerPage = number;
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
